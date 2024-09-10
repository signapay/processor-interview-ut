export interface Account {
  name: string;
  cards: { [cardNumber: string]: number };
}
