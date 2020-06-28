import React, { FC } from "react";
import { TextField } from "@material-ui/core";
import { Field } from "react-final-form";
import {
  KeyboardDatePicker,
  MuiPickersUtilsProvider,
} from "@material-ui/pickers";
import DateFnsUtils from "@date-io/date-fns";

interface Props {
  name: string;
  label: string;
  multiline?: boolean;
  rows?: number;
}

export const InputField: FC<Props> = ({ name, label, multiline, rows }) => (
  <Field<string>
    name={name}
    render={({ input, meta }) => (
      <TextField
        onChange={input.onChange}
        onBlur={input.onBlur}
        onFocus={input.onFocus}
        value={input.value}
        error={meta.error}
        label={label}
        multiline={multiline}
        rows={rows}
        name={input.name}
        variant="outlined"
        fullWidth
        margin="dense"
        size="small"
      />
    )}
  />
);
interface RenderFieldProps {
  name: string;
  label: string;
  multiline?: boolean;
  rows?: number;
}

export const RenderField: FC<RenderFieldProps> = ({ name, label, multiline, rows }) => (
  <Field<string>
    name={name}
    render={({ input }) => (
      <TextField
        disabled
        value={input.value}
        label={label}
        multiline={multiline}
        rows={rows}
        name={input.name}
        variant="outlined"
        fullWidth
        margin="dense"
        size="small"
      />
    )}
  />
);

interface DateProps {
  name: string;
  label: string;
}

export const DateField: FC<DateProps> = ({ name, label }) => (
  <MuiPickersUtilsProvider utils={DateFnsUtils}>
    <Field<string>
      name={name}
      render={({ input, meta }) => (
        <KeyboardDatePicker
          format="MM/dd/yyyy"
          onChange={input.onChange}
          onBlur={input.onBlur}
          onFocus={input.onFocus}
          name={input.name}
          value={input.value}
          error={meta.error}
          margin="dense"
          disableToolbar
          variant="inline"
          inputVariant="outlined"
          label={label}
          KeyboardButtonProps={{
            "aria-label": "change date",
          }}
        />
      )}
    />
  </MuiPickersUtilsProvider>
);
