import React, { FC } from "react";
import { Typography, Box } from "@material-ui/core";
import { Field } from "react-final-form";
import { InputField, DateField } from "./InputField";

const Form: FC = () => (
  <Box m={2}>
    <>
      <Field<string>
        name="id"
        render={({ input }) =>
          input.value && (
            <Typography component="div">
              Идентификатор: {input.value}
            </Typography>
          )
        }
      />
      <InputField name="subject" label="Тема" />
      <InputField name="body" label="Текст" multiline rows={5} />
      <DateField name="date" label="Дата" />
      <InputField name="delivered" label="Получатель" />
      <InputField name="envelop" label="Отправитель" />
      <InputField name="class" label="Класс" />
    </>
  </Box>
);

export default Form;
