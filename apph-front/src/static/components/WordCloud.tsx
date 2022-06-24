import { TagCloud } from 'react-tagcloud';
import { useNavigate } from 'react-router-dom';
import { Box } from '@mui/material';

export const WordCloud = ({
  tags
}: {
  tags: { value: string; count: number }[];
}): JSX.Element => {
  const navigate = useNavigate();
  const navigateResearch = (tagName: string) => {
    navigate('/research', { state: { tagName } });
  };
  return (
    <Box sx={{ margin: '20%', cursor: 'grab' }}>
      <TagCloud
        minSize={20}
        maxSize={100}
        tags={tags}
        className="simple-cloud"
        shuffle={false}
        onClick={(tag: { value: string; count: number }) =>
          navigateResearch(tag.value)
        }
      />
    </Box>
  );
};
