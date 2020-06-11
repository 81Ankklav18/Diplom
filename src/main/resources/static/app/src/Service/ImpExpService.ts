import { observable, action, runInAction } from "mobx";
import { MailEditItem } from "./types";
import { Mail } from "./queries";
import BaseService from "./BaseService";
import { saveAsFile } from "./contentLoad";

export default class ImpExpService extends BaseService {
  reloadTable: () => Promise<void>;
  constructor(
    notifySuccess: () => void,
    notifyError: () => void,
    reloadTable: () => Promise<void>
  ) {
    super(notifySuccess, notifyError);
    this.reloadTable = reloadTable;
  }
  @observable
  isOpen: boolean = false;
  @observable
  fileContent: string | null = null;
  @action
  openDialog = () => {
    this.isOpen = true;
    this.fileContent = null;
  };
  @action
  closeDialog = () => {
    this.isOpen = false;
    this.fileContent = null;
  };
  @action
  loadFileData = async (file: File) => {
    const reader = new FileReader();
    reader.readAsText(file, "UTF-8");
    reader.onload = (evt) => {
      runInAction(() => {
        if (evt.target?.result) {
          this.fileContent = evt.target.result as string;
        }
      });
    };
    reader.onerror = () => runInAction(this.notifyError);
  };

  @action
  importDataToDb = async () => {
    if (!this.fileContent) return;
    let data: MailEditItem[] | null = null;
    try {
      data = JSON.parse(this.fileContent);
    } catch (error) {
      this.notifyError();
    }
    if (data === null) return;
    try {
      await Mail.importData(data);
      runInAction(this.notifySuccess);
    } catch (error) {
      runInAction(this.notifyError);
    }
    await this.reloadTable();
  };
  @action
  exportDataFromDb = async () => {
    try {
      const data = await Mail.exportData();
      saveAsFile(
        "exportedData.json",
        JSON.stringify(data.data, null, "\t"),
        "application/json"
      );
      runInAction(this.notifySuccess);
    } catch (error) {
      runInAction(this.notifyError);
    }
  };
}
