import ReactWordcloud, { Scale, Spiral, Word } from 'react-wordcloud';

export const TagCloud = ({ tags }: { tags: Word[] }): JSX.Element => {
  const options = {
    enableTooltip: true,
    deterministic: true,
    fontFamily: 'impact',
    fontSizes: [20, 100] as [number, number],
    fontStyle: 'normal',
    fontWeight: 'normal',
    padding: 2,
    rotations: 0,
    scale: 'sqrt' as Scale,
    spiral: 'archimedean' as Spiral,
    transitionDuration: 1000
  };

  return (
    <div style={{ width: '100%', height: '100%' }}>
      <ReactWordcloud options={options} words={tags} />
    </div>
  );
};
