import React, { FC } from "react";
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
} from "@material-ui/core";

interface Props {
  isOpen: boolean;
  handleClose: () => void;
}

const HelpDialog: FC<Props> = ({ handleClose, isOpen }) => (
  <Dialog
    open={isOpen}
    onClose={handleClose}
    scroll="paper"
    aria-labelledby="scroll-dialog-title"
    aria-describedby="scroll-dialog-description"
  >
    <DialogTitle id="scroll-dialog-title">Справка</DialogTitle>
    <DialogContent dividers>
      <DialogContentText id="scroll-dialog-description" tabIndex={-1}>
        В семантическом дереве используется набор тегов Penn Part of Speech
        <ul>
          <li>CC - Coordinating conjunction</li>
          <li>CD - Cardinal number</li>
          <li>DT - Determiner</li>
          <li>EX - Existential there</li>
          <li>FW - Foreign word</li>
          <li>IN - Preposition or subordinating conjunction</li>
          <li>JJ - Adjective</li>
          <li>JJR - Adjective, comparative</li>
          <li>JJS - Adjective, superlative</li>
          <li>LS - List item marker</li>
          <li>MD - Modal</li>
          <li>NN - Noun, singular or mass</li>
          <li>NNS - Noun, plural</li>
          <li>NNP - Proper noun, singular</li>
          <li>NNPS - Proper noun, plural</li>
          <li>PDT - Predeterminer</li>
          <li>POS - Possessive ending</li>
          <li>PRP - Personal pronoun</li>
          <li>PRP$ - Possessive pronoun</li>
          <li>RB - Adverb</li>
          <li>RBR - Adverb, comparative</li>
          <li>RBS - Adverb, superlative</li>
          <li>RP - Particle</li>
          <li>SYM - Symbol</li>
          <li>TO - to</li>
          <li>UH - Interjection</li>
          <li>VB - Verb, base form</li>
          <li>VBD - Verb, past tense</li>
          <li>VBG - Verb, gerund or present participle</li>
          <li>VBN - Verb, past participle</li>
          <li>VBP - Verb, non-3rd person singular present</li>
          <li>VBZ - Verb, 3rd person singular present</li>
          <li>WDT - Wh-determiner</li>
          <li>WP - Wh-pronoun</li>
          <li>WP$ - Possessive wh-pronoun</li>
          <li>WRB - Wh-adverb</li>
        </ul>
      </DialogContentText>
    </DialogContent>
    <DialogActions>
      <Button onClick={handleClose} color="primary">
        Закрыть
      </Button>
    </DialogActions>
  </Dialog>
);

export default HelpDialog;
