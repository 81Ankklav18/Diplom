import React, { FC } from "react";
import { Route, Switch } from "react-router-dom";
import MailTable from "./Screen/ListMail";
import "./App.css";
import {
  Container,
  CssBaseline,
  AppBar,
  Toolbar,
  Typography,
} from "@material-ui/core";
import { StoreProvider } from "./Service/store";

const NotFound: FC = () => <div>Неккоректный маршрут</div>;

const App: FC = () => (
  <StoreProvider>
    <CssBaseline />
    <AppBar position="static">
      <Toolbar variant="dense">
        <Typography variant="h6" color="inherit">
          Mail classifier
        </Typography>
      </Toolbar>
    </AppBar>
    <Container className="root">
      <Switch>
        <Route path="/" exact component={MailTable} />
        <Route component={NotFound} />
      </Switch>
    </Container>
  </StoreProvider>
);

export default App;
