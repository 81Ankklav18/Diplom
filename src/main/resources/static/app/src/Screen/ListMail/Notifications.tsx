import React from "react";
import Snackbar from "@material-ui/core/Snackbar";
import MuiAlert, { AlertProps } from "@material-ui/lab/Alert";
import { Notification } from "../../Service/types";

function Alert(props: AlertProps) {
  return <MuiAlert elevation={6} variant="filled" {...props} />;
}

interface Props {
  close: () => void;
  notification: Notification | null;
}

export default function Notifications({ close, notification }: Props) {
  return (
    <Snackbar
      open={notification !== null}
      autoHideDuration={3000}
      onClose={close}
    >
      <Alert onClose={close} severity={notification?.type || 'info'}>
        {notification?.message || ''}
      </Alert>
    </Snackbar>
  );
}
