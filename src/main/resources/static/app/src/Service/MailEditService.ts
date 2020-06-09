import { observable, computed, action, runInAction } from "mobx";
import { MailEditItem, Id } from "./types";
import { Mail } from "./queries";
import BaseService from "./BaseService";

export default class MailEditService extends BaseService {
  reloadTable: () => Promise<void>;
  constructor(
    notifySuccess: () => void,
    notifyError: () => void,
    reloadTable: () => Promise<void>
  ) {
    super(notifySuccess, notifyError);
    this.reloadTable = reloadTable;
  }
  @observable editedMail: MailEditItem | null = null;
  @computed get isNew() {
    return this.editedMail !== null && this.editedMail.id === null;
  }
  @action
  createMail = () =>
    (this.editedMail = {
      id: null,
      subject: "",
      body: "",
      date: new Date(),
      deliveredTo: "",
      envelopeFrom: "",
      label: "",
    });
  @action
  editMail = async (id: Id) => {
    try {
      const data = await Mail.getItem(id);
      runInAction(() => {
        this.editedMail = Mail.toModel(data.data);
        this.notifySuccess();
      });
    } catch (error) {
      runInAction(this.notifyError);
      await this.reloadTable();
    };
  };
  @action
  saveEditedMail = async (item: MailEditItem) => {
    if (this.editedMail === null) return;
    const dto = Mail.toDto(item);
    const promise =
      this.editedMail.id === null ? Mail.create(dto) : Mail.update(dto);
    try {
      await promise;
      runInAction(() => {
        this.notifySuccess();
        this.editedMail = null;
      });
    } catch (error) {
      runInAction(this.notifyError);
    }
    await this.reloadTable();
  };
  @action
  cancelMailEdit = () => (this.editedMail = null);
}
