export type Id = string;

export type MailListItem = {
  id: Id;
  subject: string | null;
  snippet: string | null;
  date: string | null;
  label: string | null;
};

export type MailEditItemDto = {
  id: Id | null;
  subject: string;
  body: string;
  date: string;
  deliveredTo: string;
  envelopeFrom: string;
  label: string | null;
};

export type MailEditItem = {
  id: Id | null;
  subject: string;
  body: string;
  date: Date;
  deliveredTo: string;
  envelopeFrom: string;
  label: string | null;
};

export type LearningResult = {
  result: number;
};

export type Notification = {
  message?: string;
  type: "success" | "info" | "warning" | "error";
};

export type SimilarityResult = {
  items: MailListItem[];
  message?: string;
};

export type SemiLattice = {
  title: string;
  ids: string[];
  tree: string;
};

export type ClassificationResult = {
  precision: number;
  recall: number;
  f1: number;
  semiLatticeViewDTO: SemiLattice[];
};
