import React, { FC, useState, useCallback, useMemo } from "react";
import {
  DialogActions,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  RadioGroup,
  Radio,
  FormControlLabel,
  FormControl,
  FormLabel,
  Slider,
  Typography,
  Grid,
  Input,
  makeStyles,
} from "@material-ui/core";
import { algorithms } from "../../../Service/AnalysisStore";

interface Props {
  onSelect: (algorithmCode: string, trainPercent: number) => void;
  handleClose: () => void;
  isOpen: boolean;
}

const useStyles = makeStyles({
  input: {
    width: 42,
  },
});

const trainInputParams = {
  step: 1,
  min: 0,
  max: 100,
  type: "number",
};

const ClassificationSettings: FC<Props> = ({
  handleClose,
  onSelect,
  isOpen,
}) => {
  const classes = useStyles();
  const [value, setValue] = useState(algorithms[0]);
  const [trainPercent, setTrainPercent] = useState(50.0);
  const handleOk = useCallback(() => {
    onSelect(value, trainPercent);
  }, [onSelect, value, trainPercent]);
  const handleChange = useCallback(
    (event: React.ChangeEvent<HTMLInputElement>) => {
      setValue((event.target as HTMLInputElement).value);
    },
    []
  );
  const handleTrainChange = useCallback(
    (event: React.ChangeEvent<{}>, value: number | number[]) => setTrainPercent(+value),
    []
  );
  const handleTrainInputChange = useCallback(
    (event: React.ChangeEvent<HTMLInputElement>) => setTrainPercent(+event.target.value),
    []
  );

  const handleTrainBlur = useCallback(() => {
    if (trainPercent < 0) {
      setTrainPercent(0);
    } else if (trainPercent > 100) {
      setTrainPercent(100);
    }
  }, [trainPercent]);
  const radioItems = useMemo(
    () =>
      algorithms.map((option) => (
        <FormControlLabel
          value={option}
          key={option}
          control={<Radio />}
          label={option}
        />
      )),
    []
  );
  return (
    <Dialog
      disableBackdropClick
      disableEscapeKeyDown
      maxWidth="xs"
      aria-labelledby="confirmation-dialog-title"
      open={isOpen}
    >
      <DialogTitle id="confirmation-dialog-title">
        Настройки классификации
      </DialogTitle>
      <DialogContent dividers>
        <FormControl component="fieldset">
          <FormLabel component="legend">Алгоритм классификации</FormLabel>
          <RadioGroup
            aria-label="algorithm"
            name="algorithm"
            value={value}
            onChange={handleChange}
          >
            {radioItems}
          </RadioGroup>
        </FormControl>
        <Typography gutterBottom>
          Процент обучающей выборки
        </Typography>
        <Grid container spacing={2} alignItems="center">
          <Grid item xs>
            <Slider
              value={trainPercent}
              onChange={handleTrainChange}
              step={trainInputParams.step}
              min={trainInputParams.min}
              max={trainInputParams.max}
              valueLabelDisplay="auto"
            />
          </Grid>
          <Grid item>
            <Input
              value={trainPercent}
              margin="dense"
              onChange={handleTrainInputChange}
              onBlur={handleTrainBlur}
              className={classes.input}
              inputProps={trainInputParams}
            />
          </Grid>
        </Grid>
      </DialogContent>
      <DialogActions>
        <Button autoFocus onClick={handleClose} color="primary">
          Отмена
        </Button>
        <Button onClick={handleOk} color="primary">
          Продолжить
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default ClassificationSettings;
