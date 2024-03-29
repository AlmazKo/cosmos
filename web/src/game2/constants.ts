export const NO = 0;

export enum Dir {
  // NO = 0,
  NORTH = 'NORTH',
  EAST  = 'EAST',
  SOUTH = 'SOUTH',
  WEST  = 'WEST'
}


export const dirToString = (d: Dir | null) => {
  switch (d) {
    case Dir.NORTH:
      return "NORTH";
    case Dir.SOUTH:
      return "SOUTH";
    case Dir.WEST:
      return "WEST";
    case Dir.EAST:
      return "EAST";
    default:
      return "NO";
  }
};


export const dirToArrow = (d: Dir | null) => {
  switch (d) {
    case Dir.NORTH:
      return "↑";
    case Dir.SOUTH:
      return "↓";
    case Dir.WEST:
      return "←";
    case Dir.EAST:
      return "→";
    default:
      return "•";
  }
};

export enum TileType {
  NOTHING    = 0,
  GRASS      = 1,
  SAND       = 2,
  LAVA       = 3,
  SHALLOW    = 4,
  DEEP_WATER = 5,
  ICE        = 6,
  SNOW       = 7,
  ROAD       = 8,
  GATE       = 9,
  WALL       = 10,
  TIMBER     = 11,
}

export const stringTiles = [
  'NOTHING',
  'GRASS',
  'SAND',
  'LAVA',
  'SHALLOW',
  'DEEP_WATER',
  'ICE',
  'SNOW',
  'ROAD',
  'GATE',
  'WALL',
  'TIMBER'
];


export function debugTile(t: TileType): string {
  switch (t) {
    case TileType.NOTHING:
      return '∙';
    case TileType.GRASS:
      return '⋎';
    case TileType.SAND:
      return '∷';
    case TileType.LAVA:
      return '⋍';
    case TileType.SHALLOW:
      return '~';
    case TileType.DEEP_WATER:
      return '≈';
    case TileType.ICE:
      return '–';
    case TileType.SNOW:
      return '∴';
    case TileType.ROAD:
      return '=';
    case TileType.GATE:
      return 'П';

  }
}
