import React, { useCallback, useState, ChangeEvent, FC } from "react";
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  Typography,
} from "@material-ui/core";
import { MailEditItem } from "../../../Service/types";

interface Props {
  onImportData: (data: MailEditItem[]) => void;
  onExportData: () => void;
  isOpen: boolean;
  handleClose: () => void;
  onError: () => void;
}

const ImportExportData: FC<Props> = ({
  isOpen,
  handleClose,
  onImportData,
  onExportData,
  onError,
}) => {
  const [fileContent, setFileContent] = useState<string | null>(null);
  const onFileChoose = useCallback(
    (ev: ChangeEvent<HTMLInputElement>) => {
      const files = ev.target.files;
      if (!files) return;
      if (files.length !== 1) return;
      const file = files[0];
      const reader = new FileReader();
      reader.readAsText(file, "UTF-8");
      reader.onload = (evt) => {
        if (!evt.target) return;
        setFileContent(evt.target.result as string);
      };
      reader.onerror = (evt) => {
        onError();
      };
    },
    [onError]
  );
  const onImportClick = useCallback(() => {
    if (!fileContent) return;
    let data: MailEditItem[] | null = null;
    try {
      data = JSON.parse(fileContent);
    } catch (error) {
      onError();
    }
    if (data === null) return;
    onImportData(data);
  }, [onImportData, fileContent, onError]);
  return (
    <Dialog
      open={isOpen}
      onClose={handleClose}
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
          <Button disabled={fileContent === null} onClick={onImportClick}>
            Загрузить данные в БД
          </Button>
          <Button onClick={onExportData}>Выгрузить данные из БД</Button>
        </DialogContentText>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose} color="primary">
          Закрыть
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default ImportExportData;
