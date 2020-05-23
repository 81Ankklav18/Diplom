import React, { FC } from "react";
import Backdrop from "@material-ui/core/Backdrop";
import CircularProgress from "@material-ui/core/CircularProgress";
import { makeStyles, createStyles, Theme } from "@material-ui/core/styles";

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    backdrop: {
      zIndex: theme.zIndex.drawer + 1,
      color: "#fff",
    },
  })
);

interface Props {
  isOpen: boolean;
}

const LoadingIndicator: FC<Props> = ({ isOpen }) => {
  const classes = useStyles();
  return (
      <Backdrop className={classes.backdrop} open={isOpen}>
        <CircularProgress color="inherit" />
      </Backdrop>
  );
};

export default LoadingIndicator;
