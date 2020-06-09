import React, { FC } from "react";
import { observer } from "mobx-react";
import {
  AppBar,
  Toolbar,
  Typography,
  makeStyles,
  createStyles,
  IconButton,
  Tooltip,
  Fab,
} from "@material-ui/core";
import ReplayIcon from "@material-ui/icons/Replay";
import AddCircleOutline from "@material-ui/icons/AddCircleOutline";
import ArrowUpward from "@material-ui/icons/ArrowUpward";
import ImportExport from "@material-ui/icons/ImportExport";
import { useStore } from "../Service/store";
import RunButton from "./RunButton";

const useStyles = makeStyles((theme) => ({
  offset: theme.mixins.toolbar,
  ...createStyles({
    header: {
      flexGrow: 1,
    },
    toTopIcon: {
      bottom: "20px",
      position: "fixed",
      left: "20px",
      zIndex: 100,
    },
  }),
}));

const scrollToTop = () => window.scrollTo(0, 0);

const TopBar: FC = observer(() => {
  const classes = useStyles();
  const store = useStore();
  return (
    <>
      <AppBar position="fixed">
        <Toolbar variant="dense">
          <Typography variant="h6" color="inherit" className={classes.header}>
            Mail classifier
          </Typography>
          <Tooltip title="Импорт/Экспорт">
            <IconButton
              aria-label="import and export"
              color="inherit"
              onClick={store.impExpStore.openDialog}
            >
              <ImportExport />
            </IconButton>
          </Tooltip>
          <Tooltip title="Добавить письмо">
            <IconButton
              aria-label="add item"
              color="inherit"
              onClick={store.mailEditStore.createMail}
            >
              <AddCircleOutline />
            </IconButton>
          </Tooltip>
          <Tooltip title="Перезагрузить таблицу">
            <IconButton
              aria-label="reload items"
              color="inherit"
              onClick={store.reloadTable}
            >
              <ReplayIcon />
            </IconButton>
          </Tooltip>
          <RunButton
            startClassification={store.classificationStore.toSettings}
            startSimilaritySearch={store.similarityStore.toSettings}
            isSimilarityDisabled={store.isSimilarityDisabled}
          />
        </Toolbar>
      </AppBar>
      <Fab
        variant="extended"
        onClick={scrollToTop}
        size="small"
        color="primary"
        className={classes.toTopIcon}
      >
        <ArrowUpward />
      </Fab>
      <div className={classes.offset} />
    </>
  );
});

export default TopBar;
