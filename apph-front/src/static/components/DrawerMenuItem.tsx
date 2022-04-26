import { Link } from 'react-router-dom';
import { ListItem, ListItemIcon, ListItemText } from '@mui/material';

export const DrawerMenuItem = ({
                                   title,
                                   url,
                                   icon
                               }: {
    title: string;
    url: string;
    icon: JSX.Element;
}): JSX.Element => {
    return (
        <ListItem button component={Link} to={url}>
            <ListItemIcon>{icon}</ListItemIcon>
            <ListItemText primary={title} />
        </ListItem>
    );
};