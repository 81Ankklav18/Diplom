import { observable, computed, action, runInAction } from "mobx";
import { MailListItem, MailEditItem, Id, Notification } from "./types";
import { Mail } from "./queries";
import AnalysisStore from "./AnalysisStore";

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

  @action
  getSelectedIds = () => this.selectedIds;
  analysisStore = new AnalysisStore(
    this.getSelectedIds,
    this.notifySuccess,
    this.notifyError
  );

  @observable rows: MailListItem[] = [];
  @observable editedMail: MailEditItem | null = null;
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
  removeSelected = async () => {
    try {
      await Mail.remove(this.selectedIds);
      runInAction(() => {
        this.notifySuccess();
        this.editedMail = null;
      });
    } catch (error) {
      runInAction(this.notifyError);
    }
    await this.reloadTable();
  };

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
    }
    await this.reloadTable();
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

  @observable
  isOpenImportExport: boolean = false;
  @action
  openImpExpDialog = () => (this.isOpenImportExport = true);
  @action
  closeImpExpDialog = () => (this.isOpenImportExport = false);
  @action
  importDataToDb = async (data: MailEditItem[]) => {
    try {
      await Mail.importData(data);
      runInAction(this.notifySuccess);
    } catch (error) {
      runInAction(this.notifyError);
    }
    await this.reloadTable();
  };
  @action
  exportDataFromDb = () => {
    window.open("/mail/export", "_blank");
  };
}
