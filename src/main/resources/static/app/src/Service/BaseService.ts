export default class BaseService {
    notifySuccess: () => void;
    notifyError: () => void;
    constructor(
      notifySuccess: () => void,
      notifyError: () => void
    ) {
      this.notifySuccess = notifySuccess;
      this.notifyError = notifyError;
    }
}