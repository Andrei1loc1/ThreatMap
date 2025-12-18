import { useEffect, useRef } from "react";
import mapboxgl from "mapbox-gl/dist/mapbox-gl-csp";
import MapboxWorker from "mapbox-gl/dist/mapbox-gl-csp-worker?worker";
import "mapbox-gl/dist/mapbox-gl.css";
import { AttackEvent } from "@/utils/AttackEvent";

mapboxgl.workerClass = MapboxWorker as any;

// Persist view state between mounts so the map stays where the user left it.
let lastViewState: {
  center: [number, number];
  zoom: number;
  pitch: number;
  bearing: number;
} | null = null;

const MAPBOX_TOKEN = import.meta.env.VITE_MAPBOX_TOKEN;

if (!MAPBOX_TOKEN) {
  console.error("Missing VITE_MAPBOX_TOKEN env variable for Mapbox access.");
} else {
  mapboxgl.accessToken = MAPBOX_TOKEN;
}

interface Mapbox3DProps {
  flaggedIps: AttackEvent[];
}

export const Mapbox3D = ({ flaggedIps }: Mapbox3DProps) => {
  const mapContainerRef = useRef<HTMLDivElement | null>(null);
  const mapRef = useRef<mapboxgl.Map | null>(null);
  const previousFlaggedIpsRef = useRef<AttackEvent[]>([]);

  const createGeoJsonData = (ips: AttackEvent[]) => ({
    type: 'FeatureCollection' as const,
    features: ips
      .filter(ip => ip.location && !isNaN(ip.location.longitudine) && !isNaN(ip.location.latitudine))
      .map(ip => ({
        type: 'Feature' as const,
        geometry: {
          type: 'Point' as const,
          coordinates: [ip.location!.longitudine, ip.location!.latitudine],
        },
        properties: {
          ip: ip.adresaIP,
          failures: ip.failedAttempts,
        },
      })),
  });

  useEffect(() => {
    if (!MAPBOX_TOKEN) return;
    if (!mapContainerRef.current) return;

    const map = new mapboxgl.Map({
      container: mapContainerRef.current,
      style: "mapbox://styles/mapbox/dark-v11",
      center: lastViewState?.center ?? [0, 0],
      zoom: lastViewState?.zoom ?? 1.5,
      pitch: lastViewState?.pitch ?? 80,
      bearing: lastViewState?.bearing ?? -45,
      projection: "globe",
    });

    mapRef.current = map;

    map.on("style.load", () => {
      map.setFog({
        color: "rgb(0, 0, 0)",
        "high-color": "rgb(15, 15, 20)",
        "space-color": "rgb(5, 5, 5)",
        "horizon-blend": 0.2,
      });

      map.addSource('failure-ips', {
        type: 'geojson',
        data: createGeoJsonData(flaggedIps),
      });

      map.addLayer({
        id: 'failure-markers',
        type: 'circle',
        source: 'failure-ips',
        paint: {
          'circle-radius': 8,
          'circle-color': '#ff0000',
          'circle-stroke-width': 2,
          'circle-stroke-color': '#ffffff',
        },
      });

      const popup = new mapboxgl.Popup({
        closeButton: false,
        closeOnClick: false,
      });

      map.on('mouseenter', 'failure-markers', (e) => {
        if (e.features && e.features[0] && e.features[0].properties) {
          const properties = e.features[0].properties;
          popup
            .setLngLat(e.lngLat)
            .setHTML(`<strong>IP:</strong> ${properties.ip}<br><strong>Failures:</strong> ${properties.failures}`)
            .addTo(map);
        }
      });

      map.on('mouseleave', 'failure-markers', () => {
        popup.remove();
      });

      map.on("moveend", () => {
        const center = map.getCenter();
        lastViewState = {
          center: [center.lng, center.lat],
          zoom: map.getZoom(),
          pitch: map.getPitch(),
          bearing: map.getBearing(),
        };
      });
    });

    return () => map.remove();
  }, []);

  useEffect(() => {
    if (mapRef.current && mapRef.current.isStyleLoaded()) {
      const source = mapRef.current.getSource('failure-ips');
      if (source && 'setData' in source) {
        (source as mapboxgl.GeoJSONSource).setData(createGeoJsonData(flaggedIps));
      }

      const previousIps = previousFlaggedIpsRef.current;
      const newIps = flaggedIps.filter(ip => !previousIps.some(prev => prev.adresaIP === ip.adresaIP));
      if (newIps.length > 0) {
        const lastNewIp = newIps[newIps.length - 1];
        if (lastNewIp.location && !isNaN(lastNewIp.location.longitudine) && !isNaN(lastNewIp.location.latitudine)) {
          mapRef.current.flyTo({
            center: [lastNewIp.location.longitudine, lastNewIp.location.latitudine],
            zoom: 10,
            pitch: 80,
            bearing: -45,
          });
        }
      }

      previousFlaggedIpsRef.current = flaggedIps;
    }
  }, [flaggedIps]);

  return (
    <div
      ref={mapContainerRef}
      style={{ width: "100%", height: "100%" }}
    />
  );
};
