import React, { FC, useCallback, memo } from "react";
import { Slider, Grid, Input, makeStyles } from "@material-ui/core";

const useStyles = makeStyles({
  input: {
    width: 42,
  },
});

const sliderParams = {
  step: 1,
  min: 0,
  max: 100,
  type: "number",
};
interface Props {
  onChange: (value: number) => void;
  value: number;
}

const SliderInput: FC<Props> = memo(({ value, onChange }) => {
  const classes = useStyles();
  const handleTrainInputChange = useCallback(
    (event: React.ChangeEvent<HTMLInputElement>) =>
      onChange(+event.target.value),
    [onChange]
  );
  const handleTrainBlur = () => {
    if (value < 0) {
      onChange(0);
    } else if (value > 100) {
      onChange(100);
    }
  };
  const handleTrainChange = useCallback(
    (_: any, value: number | number[]) => onChange(+value),
    [onChange]
  );
  return (
    <Grid container spacing={2} alignItems="center">
      <Grid item xs>
        <Slider
          value={value}
          onChange={handleTrainChange}
          step={sliderParams.step}
          min={sliderParams.min}
          max={sliderParams.max}
          valueLabelDisplay="auto"
        />
      </Grid>
      <Grid item>
        <Input
          value={value}
          margin="dense"
          onChange={handleTrainInputChange}
          onBlur={handleTrainBlur}
          className={classes.input}
          inputProps={sliderParams}
        />
      </Grid>
    </Grid>
  );
});

export default SliderInput;
