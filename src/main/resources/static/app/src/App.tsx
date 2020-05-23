import React, { FC } from "react";
import { Route, Switch } from "react-router-dom";
import { Container, CssBaseline } from "@material-ui/core";
import "./App.css";
import MailTable from "./Screen/ListMail";
import { StoreProvider } from "./Service/store";
import TopBar from "./Screen/TopBar";

const NotFound: FC = () => <div>Неккоректный маршрут</div>;

const App: FC = () => (
  <StoreProvider>
    <CssBaseline />
    <TopBar />
    <Container className="root">
      <Switch>
        <Route path="/" exact component={MailTable} />
        <Route component={NotFound} />
      </Switch>
    </Container>
  </StoreProvider>
);

export default App;
