import React, { useCallback, ChangeEvent, FC, memo } from "react";
import { observer } from "mobx-react";
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Typography,
  Grid,
  IconButton,
  Tooltip,
} from "@material-ui/core";
import PublishIcon from "@material-ui/icons/Publish";
import { useStore } from "../../../Service/store";

type UploadButtonProps = {
  disabled: boolean;
  onClick: () => void;
};
const UploadButton: FC<UploadButtonProps> = memo(({ disabled, onClick }) => (
  <Tooltip title="Загрузить в БД">
    <span>
      <IconButton aria-label="publish" disabled={disabled} onClick={onClick}>
        <PublishIcon />
      </IconButton>
    </span>
  </Tooltip>
));

const ImportExportData: FC = observer(() => {
  const { impExpStore: store } = useStore();
  const onFileJsonChoose = useCallback(
    (ev: ChangeEvent<HTMLInputElement>) => {
      const files = ev.target.files;
      if (!files || files.length !== 1) return;
      store.loadFileDataJson(files[0]);
    },
    [store]
  );
  const onFileCsvChoose = useCallback(
    (ev: ChangeEvent<HTMLInputElement>) => {
      const files = ev.target.files;
      if (!files || files.length !== 1) return;
      store.loadFileCsv(files[0]);
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
        <Grid container spacing={1}>
          <Grid item xs={12}>
            <Typography variant="h6">Импорт данных</Typography>
            <Typography>Выберите файл с данными для добавления в БД</Typography>
          </Grid>
          <Grid item xs={6}>
            <Button variant="contained" component="label">
              Выберите JSON
              <input
                type="file"
                accept="application/json"
                style={{ display: "none" }}
                onChange={onFileJsonChoose}
              />
            </Button>
            <UploadButton
              disabled={store.fileJson === null}
              onClick={store.importDataToDbJson}
            />
          </Grid>
          <Grid item xs={6}>
            <Button variant="contained" component="label">
              Выберите CSV
              <input
                type="file"
                accept="text/csv"
                style={{ display: "none" }}
                onChange={onFileCsvChoose}
              />
            </Button>
            <UploadButton
              disabled={store.fileCsv === null}
              onClick={store.importDataToDbCsv}
            />
          </Grid>
          <Grid item xs={12}>
            <Typography variant="h6">Экспорт данных</Typography>
            <Typography>
              Выгрузка данных из БД в формате JSON или CSV
            </Typography>
          </Grid>
          <Grid item xs={6}>
            <Button variant="contained" onClick={store.exportDataFromDbJson}>
              Выгрузить JSON
            </Button>
          </Grid>
          <Grid item xs={6}>
            <Button variant="contained" onClick={store.exportDataFromDbCsv}>
              Выгрузить CSV
            </Button>
          </Grid>
        </Grid>
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
