import { observable, action, runInAction } from "mobx";
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
  fileJson: File | null = null;
  @observable
  fileCsv: File | null = null;
  @action
  openDialog = () => {
    this.isOpen = true;
    this.fileJson = null;
    this.fileCsv = null;
  };
  @action
  closeDialog = () => {
    this.isOpen = false;
    this.fileJson = null;
    this.fileCsv = null;
  };
  @action
  loadFileDataJson = async (file: File) => (this.fileJson = file);
  @action
  loadFileCsv = (file: File) => (this.fileCsv = file);

  @action
  importDataToDbJson = async () => {
    if (!this.fileJson) return;
    const formData = new FormData();
    formData.append("file", this.fileJson, "import.json");
    try {
      await Mail.importDataJson(formData);
      runInAction(this.notifySuccess);
    } catch (error) {
      runInAction(this.notifyError);
    }
    await this.reloadTable();
  };

  @action
  importDataToDbCsv = async () => {
    if (!this.fileCsv) return;
    const formData = new FormData();
    formData.append("file", this.fileCsv, "import.csv");
    try {
      await Mail.importDataCsv(formData);
      runInAction(this.notifySuccess);
    } catch (error) {
      runInAction(this.notifyError);
    }
    await this.reloadTable();
  };

  @action
  exportDataFromDbJson = async () => {
    try {
      const data = await Mail.exportDataJson();
      saveAsFile("exportedData.json", data.data, "application/json");
      runInAction(this.notifySuccess);
    } catch (error) {
      runInAction(this.notifyError);
    }
  };
  @action
  exportDataFromDbCsv = async () => {
    try {
      const data = await Mail.exportDataCsv();
      saveAsFile("exportedData.csv", data.data, "text/csv");
      runInAction(this.notifySuccess);
    } catch (error) {
      runInAction(this.notifyError);
    }
  };
}
