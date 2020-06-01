import React, { FC } from "react";
import { Box } from "@material-ui/core";
import { observer } from "mobx-react";
import { Form } from "react-final-form";
import { useStore } from "../../../Service/store";
import { MailEditItem } from "../../../Service/types";
import Header from "./Header";
import FormFields from "./Form";

const EditMail: FC = observer(() => {
  const store = useStore();
  if (store.editedMail === null) return null;
  return (
    <Box>
      <Form<MailEditItem>
        initialValues={store.editedMail}
        onSubmit={store.saveEditedMail}
        subscription={{ pristine: true, submitting: true }}
        render={({ handleSubmit, submitting }) => (
          <form onSubmit={handleSubmit}>
            <Header
              cancel={store.cancelMailEdit}
              isNew={store.isNew}
              submitting={submitting}
            />
            <FormFields isNew={store.isNew} />
          </form>
        )}
      />
    </Box>
  );
});

export default EditMail;
