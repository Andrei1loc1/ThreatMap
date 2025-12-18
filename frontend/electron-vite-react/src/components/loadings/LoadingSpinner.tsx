import React from 'react'

interface LoadingSpinnerProps {
  text?: string
}

const LoadingSpinner: React.FC<LoadingSpinnerProps> = ({ text = "Loading..." }) => (
  <div className="flex items-center justify-center">
    <div className="loading-spinner"></div>
    <p className='text-sm text-gray-400 ml-2'>{text}</p>
  </div>
)

export default LoadingSpinner