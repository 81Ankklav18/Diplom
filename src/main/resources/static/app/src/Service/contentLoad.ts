export function saveAsFile(filename: string, data: any, type: string) {
  const blob = new Blob([data], { type });
  if (window.navigator.msSaveOrOpenBlob !== undefined) {
    window.navigator.msSaveBlob(blob, filename);
  } else {
    const elem = window.document.createElement("a");
    const url = window.URL.createObjectURL(blob);
    elem.href = url;
    elem.download = filename;
    document.body.appendChild(elem);
    setTimeout(() => {
      elem.click();
      document.body.removeChild(elem);
      URL.revokeObjectURL(url);
    }, 50);
  }
}

export const contentType = {
  json: "application/json",
  csv: "text/csv",
};
