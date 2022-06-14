import { TagCloud } from 'react-tagcloud';

export const WordCloud = ({
  tags
}: {
  tags: { value: string; count: number }[];
}): JSX.Element => {
  return (
    <div
      style={{
        margin: '20%'
      }}
    >
      <TagCloud
        minSize={20}
        maxSize={100}
        tags={tags}
        className="simple-cloud"
        shuffle={false}
      />
    </div>
  );
};
