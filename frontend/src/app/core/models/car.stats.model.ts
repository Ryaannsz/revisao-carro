import { CarOption } from "./car.option.model";
import { SeriesBlock } from "./series.block.model";

export interface CarStatsResponse {
  cars: CarOption[];
  labels: string[];       
  gasto: SeriesBlock;
  litros: SeriesBlock;
  eficiencia: SeriesBlock;
  mode: 'sum' | 'compare';
}