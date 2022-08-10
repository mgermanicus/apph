import { IconButton } from '@mui/material';
import { Delete } from '@mui/icons-material';
import UserService from '../../services/UserService';

export const DeleteUser = ({
  userId,
  setRefresh
}: {
  userId: string;
  setRefresh: React.Dispatch<React.SetStateAction<boolean>>;
}) => {
  const handleClick = () => {
    UserService.deleteUser(
      userId,
      () => setRefresh((refresh) => !refresh),
      (error) => console.log(error)
    );
  };
  return (
    <IconButton onClick={handleClick}>
      <Delete />
    </IconButton>
  );
};
