import React, { FC } from "react";
import { Container, CssBaseline } from "@material-ui/core";
import "./App.css";
import MailTable from "./Screen/ListMail";
import { StoreProvider } from "./Service/store";
import TopBar from "./Screen/TopBar";

const App: FC = () => (
  <StoreProvider>
    <CssBaseline />
    <TopBar />
    <Container className="root">
      <MailTable />
    </Container>
  </StoreProvider>
);

export default App;
