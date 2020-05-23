import React, { FC } from "react";
import {
  createStyles,
  lighten,
  makeStyles,
  Theme,
} from "@material-ui/core/styles";
import { Typography, Toolbar, Tooltip, IconButton } from "@material-ui/core";
import DoneIcon from "@material-ui/icons/Done";
import CancelIcon from "@material-ui/icons/Cancel";

const useToolbarStyles = makeStyles((theme: Theme) =>
  createStyles({
    highlight:
      theme.palette.type === "light"
        ? {
            color: theme.palette.primary.main,
            backgroundColor: lighten(theme.palette.primary.light, 0.85),
          }
        : {
            color: theme.palette.text.primary,
            backgroundColor: theme.palette.secondary.dark,
          },
    title: {
      flex: "1 1 100%",
    },
  })
);
type Props = {
  cancel: () => void;
  isNew: boolean;
};
const EditMail: FC<Props> = ({ isNew, cancel }) => {
  const classes = useToolbarStyles();
  return (
    <Toolbar className={classes.highlight}>
      <Typography className={classes.title} variant="h6" component="div">
        {isNew ? "Создание" : "Редактирование"}
      </Typography>
      <Tooltip title="Сохранить">
        <IconButton aria-label="save" type="submit">
          <DoneIcon />
        </IconButton>
      </Tooltip>
      <Tooltip title="Отмена">
        <IconButton aria-label="cancel" onClick={cancel}>
          <CancelIcon />
        </IconButton>
      </Tooltip>
    </Toolbar>
  );
};

export default EditMail;
