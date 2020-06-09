import React, { useCallback, ChangeEvent, FC } from "react";
import { observer } from "mobx-react";
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  Typography,
} from "@material-ui/core";
import { useStore } from "../../../Service/store";

const ImportExportData: FC = observer(() => {
  const { impExpStore: store } = useStore();
  const onFileChoose = useCallback(
    (ev: ChangeEvent<HTMLInputElement>) => {
      const files = ev.target.files;
      if (!files || files.length !== 1) return;
      store.loadFileData(files[0]);
    },
    [store]
  );
  return (
    <Dialog
      open={store.isOpen}
      onClose={store.closeDialog}
      scroll="paper"
      aria-labelledby="scroll-dialog-title"
      aria-describedby="scroll-dialog-description"
    >
      <DialogTitle id="scroll-dialog-title">Импорт/Экспорт</DialogTitle>
      <DialogContent dividers>
        <DialogContentText id="scroll-dialog-description" tabIndex={-1}>
          <Typography>Загрузите файл для добавления в БД (json)</Typography>
          <Button variant="contained" component="label">
            Выберите файл
            <input
              type="file"
              style={{ display: "none" }}
              onChange={onFileChoose}
            />
          </Button>
          <Button
            disabled={store.fileContent === null}
            onClick={store.importDataToDb}
          >
            Загрузить данные в БД
          </Button>
          <Button onClick={store.exportDataFromDb}>
            Выгрузить данные из БД
          </Button>
        </DialogContentText>
      </DialogContent>
      <DialogActions>
        <Button onClick={store.closeDialog} color="primary">
          Закрыть
        </Button>
      </DialogActions>
    </Dialog>
  );
});

export default ImportExportData;
