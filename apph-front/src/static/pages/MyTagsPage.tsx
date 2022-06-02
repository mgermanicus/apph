import React from 'react';
import ReactWordcloud from 'react-wordcloud';

export const MyTagsPage = (): JSX.Element => {
  const words = [
    {
      text: 'told',
      value: 64
    },
    {
      text: 'mistake',
      value: 11
    },
    {
      text: 'thought',
      value: 16
    },
    {
      text: 'bad',
      value: 17
    }
  ];

  const options = {
    colors: ['#1f77b4', '#ff7f0e', '#2ca02c', '#d62728', '#9467bd', '#8c564b'],
    enableTooltip: true,
    deterministic: false,
    fontFamily: 'impact',
    fontSizes: [20, 100] as [number, number],
    fontStyle: 'normal',
    fontWeight: 'normal',
    padding: 0,
    rotations: 0,
    rotationAngles: [0, 0] as [number, number],
    transitionDuration: 1000
  };

  return (
    <div>
      <div style={{ height: 400, width: 400, alignSelf: 'center' }}>
        <ReactWordcloud options={options} words={words} />
      </div>
    </div>
  );
};
