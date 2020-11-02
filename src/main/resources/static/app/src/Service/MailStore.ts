import { observable, computed, action, runInAction } from "mobx";
import { MailListItem, Id, Notification } from "./types";
import { Mail } from "./queries";
import SimilarityStore from "./SimilarityAnalysis";
import ClassificationStore from "./ClassificationAnalysis";
import ImpExpService from "./ImpExpService";
import MailEditService from "./MailEditService";
import HelpService from "./HelpService";

export default class MailStore {
  @observable selectedIds: Id[] = [];
  @computed get isSimilarityDisabled() {
    return this.selectedIds.length !== 1;
  }
  @observable notification: Notification | null = null;
  @action
  closeNotification = () => (this.notification = null);
  @action
  notifyError = () =>
    (this.notification = {
      message: "Произошла ошибка",
      type: "error",
    });
  @action
  notifySuccess = () =>
    (this.notification = {
      message: "Операция выполнена",
      type: "success",
    });
  @observable rows: MailListItem[] = [];
  @action
  toggleSelectAll = (isAll: boolean) =>
    (this.selectedIds = isAll ? this.rows.map((n) => n.id) : []);
  @action
  selectItem = (id: Id) => {
    const selectedIndex = this.selectedIds.indexOf(id);
    let newSelected: Id[] = [];
    if (selectedIndex === -1) {
      newSelected = newSelected.concat(this.selectedIds, id);
    } else if (selectedIndex === 0) {
      newSelected = newSelected.concat(this.selectedIds.slice(1));
    } else if (selectedIndex === this.selectedIds.length - 1) {
      newSelected = newSelected.concat(this.selectedIds.slice(0, -1));
    } else if (selectedIndex > 0) {
      newSelected = newSelected.concat(
        this.selectedIds.slice(0, selectedIndex),
        this.selectedIds.slice(selectedIndex + 1)
      );
    }
    this.selectedIds = newSelected;
  };
  @action
  reloadTable = async () => {
    try {
      const data = await Mail.getAll();
      runInAction(() => {
        this.rows = data.data;
        const set = new Set(data.data.map((e) => e.id));
        this.selectedIds = this.selectedIds.filter((e) => set.has(e));
        this.notifySuccess();
      });
    } catch (error) {
      runInAction(() => {
        this.notifyError();
        this.rows = [];
      });
    }
  };

  @action
  getSelectedIds = () => this.selectedIds;
  classificationStore = new ClassificationStore(
    this.getSelectedIds,
    this.notifySuccess,
    this.notifyError
  );
  similarityStore = new SimilarityStore(
    this.getSelectedIds,
    this.notifySuccess,
    this.notifyError
  );
  impExpStore = new ImpExpService(
    this.notifySuccess,
    this.notifyError,
    this.reloadTable
  );
  mailEditStore = new MailEditService(
    this.notifySuccess,
    this.notifyError,
    this.reloadTable
  );
  helpStore = new HelpService();
  @action
  removeSelected = async () => {
    try {
      await Mail.remove(this.selectedIds);
      runInAction(() => {
        this.notifySuccess();
        this.mailEditStore.editedMail = null;
      });
    } catch (error) {
      runInAction(this.notifyError);
    }
    await this.reloadTable();
  };
}
