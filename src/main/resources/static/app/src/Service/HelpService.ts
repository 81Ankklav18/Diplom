import { observable, action } from "mobx";

export default class HelpService {
  @observable isOpen: boolean = false;
  @action
  open = () => (this.isOpen = true);
  @action
  close = () => (this.isOpen = false);
}
