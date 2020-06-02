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
import Analysis from "./Analysis";
import ImportExportData from "./ImportExportData";

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
      <ImportExportData
        handleClose={store.closeImpExpDialog}
        isOpen={store.isOpenImportExport}
        onError={store.notifyError}
        onExportData={store.exportDataFromDb}
        onImportData={store.importDataToDb}
      />
      <Drawer
        className={classes.drawer}
        open={store.editedMail !== null}
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
      <Analysis />
    </>
  );
});

export default MailRoot;
