import React, { FC } from "react";
import { Typography, Box } from "@material-ui/core";
import { Field } from "react-final-form";
import { InputField, DateField, RenderField } from "./InputField";

type Props = { isNew: boolean };
const Form: FC<Props> = ({ isNew }) => (
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
      <InputField name="deliveredTo" label="Получатель" />
      <InputField name="envelopeFrom" label="Отправитель" />
      <InputField name="label" label="Класс" />
      {isNew && <RenderField name="tree" label="Дерево" multiline rows={20} />}
    </>
  </Box>
);

export default Form;
