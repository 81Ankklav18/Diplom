import React, { FC } from "react";
import {
  Backdrop,
  CircularProgress,
  makeStyles,
  createStyles,
  Theme,
} from "@material-ui/core";

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
