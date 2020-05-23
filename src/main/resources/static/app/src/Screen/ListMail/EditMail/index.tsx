import React, { FC } from "react";
import { Box } from "@material-ui/core";
import { observer } from "mobx-react";
import { Form } from "react-final-form";
import Header from "./Header";
import FormFields from "./Form";
import { useStore } from "../../../Service/store";
import { MailEditItem } from "../../../Service/types";

const EditMail: FC = observer(() => {
  const store = useStore();
  if (store.editedMail === null) return null;
  return (
    <Box>
      <Form<MailEditItem>
        initialValues={store.editedMail}
        onSubmit={store.saveEditedMail}
        subscription={{ pristine: true }}
        render={({ handleSubmit }) => (
          <form onSubmit={handleSubmit}>
            <Header cancel={store.cancelMailEdit} isNew={store.isNew} />
            <FormFields />
          </form>
        )}
      />
    </Box>
  );
});

export default EditMail;
