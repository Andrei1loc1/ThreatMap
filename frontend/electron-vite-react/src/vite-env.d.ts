/// <reference types="vite/client" />

interface Window {
  // expose in the `electron/preload/index.ts`
  ipcRenderer: import('electron').IpcRenderer
}

declare module "mapbox-gl/dist/mapbox-gl-csp" {
  export * from "mapbox-gl";
  export { default } from "mapbox-gl";
}

declare module "mapbox-gl/dist/mapbox-gl-csp-worker?worker" {
  const MapboxWorker: {
    new (): Worker;
  };

  export default MapboxWorker;
}
