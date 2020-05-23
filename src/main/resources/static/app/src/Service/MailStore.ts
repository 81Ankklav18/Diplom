import { observable, computed, action, runInAction } from "mobx";
import { MailListItem, MailEditItem, Id, Notification } from "./types";
import { Mail } from "./queries";

type ValueOf<T> = T[keyof T];
const stateLoading = {
  pending: "pending",
  done: "done",
  error: "error",
} as const;
type StateLoading = ValueOf<typeof stateLoading>;

const errorMessage = (): Notification => ({
  message: "Произошла ошибка",
  type: "error",
});
const successMessage = (): Notification => ({
  message: "Операция выполнена",
  type: "success",
});

const testData = (): MailListItem => ({
  id: Math.random().toString(),
  subject: "",
  snippet: "",
  date: new Date().toISOString(),
  class: "",
});

export default class MailStore {
  @observable notification: Notification | null = null;
  @observable rows: MailListItem[] = [];
  @observable selectedIds: Id[] = [];
  @observable editedMail: MailEditItem | null = null;
  @observable stateLoading: StateLoading = "done";
  @observable analysisResult: string | null = null;
  @observable analysisLoading: boolean = false;

  @computed get selectedCount() {
    return this.selectedIds.length;
  }
  @computed get isNew() {
    return this.editedMail !== null && this.editedMail.id === null;
  }

  @action
  closeNotification = () => (this.notification = null);

  @action
  reloadTable = async () => {
    this.stateLoading = "pending";
    try {
      const data = await Mail.getAll();
      runInAction(() => {
        this.stateLoading = "done";
        this.rows = data.data;
        const set = new Set(data.data.map((e) => e.id));
        this.selectedIds = this.selectedIds.filter((e) => set.has(e));
        this.notification = successMessage();
      });
    } catch (error) {
      runInAction(() => {
        this.stateLoading = "error";
        this.notification = errorMessage();
        this.rows = [testData(),testData(),testData(),testData(),testData(),testData(),testData(),testData(),testData()];
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
    this.stateLoading = "pending";
    try {
      await Mail.remove(this.selectedIds);
      runInAction(() => {
        this.stateLoading = "done";
        this.notification = successMessage();
        this.editedMail = null;
      });
    } catch (error) {
      runInAction(() => {
        this.stateLoading = "error";
        this.notification = errorMessage();
      });
    }
    await this.reloadTable();
  };

  @action
  createMail = () =>
    (this.editedMail = {
      id: null,
      subject: "",
      body: "",
      date: new Date(),
      delivered: "",
      envelop: "",
      class: "",
    });
  @action
  editMail = async (id: Id) => {
    this.stateLoading = "pending";
    try {
      const data = await Mail.getItem(id);
      runInAction(() => {
        this.stateLoading = "done";
        this.editedMail = Mail.toModel(data.data);
        this.notification = successMessage();
      });
    } catch (error) {
      runInAction(() => {
        this.stateLoading = "error";
        this.notification = errorMessage();
      });
    }
    await this.reloadTable();
  };
  @action
  saveEditedMail = async (item: MailEditItem) => {
    if (this.editedMail === null) return;
    const dto = Mail.toDto(item);
    const promise =
      this.editedMail.id === null ? Mail.create(dto) : Mail.update(dto);
    this.stateLoading = "pending";
    try {
      await promise;
      runInAction(() => {
        this.stateLoading = "done";
        this.notification = successMessage();
        this.editedMail = null;
      });
    } catch (error) {
      runInAction(() => {
        this.stateLoading = "error";
        this.notification = errorMessage();
      });
    }
    await this.reloadTable();
  };
  @action
  cancelMailEdit = () => (this.editedMail = null);

  @action
  analyze = async () => {
    this.stateLoading = "pending";
    this.analysisLoading = true;
    try {
      const data = await Mail.analyze(this.selectedIds);
      runInAction(() => {
        this.stateLoading = "done";
        this.analysisResult = JSON.stringify(data.data, null, "\t");
        this.notification = successMessage();
      });
    } catch (error) {
      runInAction(() => {
        this.stateLoading = "error";
        this.notification = errorMessage();
      });
    }
    runInAction(() => {
      this.analysisLoading = false;
    });
    await this.reloadTable();
  };
  @action
  analyzeResultClose = () => (this.analysisResult = null);
}
