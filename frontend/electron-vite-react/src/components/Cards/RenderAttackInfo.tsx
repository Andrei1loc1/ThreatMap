import React from 'react'

interface RenderAttackInfoProps{
    fc: () => JSX.Element
}

const RenderAttackInfo : React.FC<RenderAttackInfoProps> = ({fc}) => {
    return (
        <div className='light-card h-24 flex flex-col justify-center p-4 my-5'>
            {fc()}
        </div>
    )
}
export default RenderAttackInfo
