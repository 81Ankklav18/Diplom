import React, { FC } from "react";
import { observer } from "mobx-react";
import {
  Drawer,
  Divider,
  makeStyles,
  createStyles,
  Theme,
} from "@material-ui/core";
import MailList from "./MailList";
import EditMail from "./EditMail";
import { useStore } from "../../Service/store";
import Notifications from "./Notifications";
import ImportExportData from "./ImportExportData";
import Similarity from "./Similarity";
import Classification from "./Classification";

const drawerWidth = 450;

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      display: "flex",
    },
    appBar: {
      width: `calc(100% - ${drawerWidth}px)`,
      marginLeft: drawerWidth,
    },
    drawer: {
      width: drawerWidth,
      flexShrink: 0,
    },
    drawerPaper: {
      width: drawerWidth,
    },
    // necessary for content to be below app bar
    toolbar: theme.mixins.toolbar,
    content: {
      flexGrow: 1,
      backgroundColor: theme.palette.background.default,
      padding: theme.spacing(3),
    },
  })
);

const MailRoot: FC = observer(() => {
  const store = useStore();
  const classes = useStyles();
  return (
    <>
      <MailList />
      <Drawer
        className={classes.drawer}
        open={store.mailEditStore.editedMail !== null}
        variant="temporary"
        classes={{
          paper: classes.drawerPaper,
        }}
        anchor="right"
      >
        <div className={classes.toolbar} />
        <Divider />
        <EditMail />
      </Drawer>
      <Notifications
        close={store.closeNotification}
        notification={store.notification}
      />
      <Similarity />
      <Classification />
      <ImportExportData />
    </>
  );
});

export default MailRoot;
