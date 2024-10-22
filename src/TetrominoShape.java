// Aaron Ye
// 2023-06-18
// Tetromino Shape
// Represents a single tetromino
public class TetrominoShape implements Cloneable {
    // Possible shapes
    public enum Shapes {
        I, J, L, O, S, T, Z
    }

    // Possible rotations for each shape
    private final static int[][][][] TETROMINO_SHAPE_ARRAYS = {
            // I
            {
                    {
                            {0, 0, 0, 0},
                            {1, 1, 1, 1},
                            {0, 0, 0, 0},
                            {0, 0, 0, 0}
                    },
                    {
                            {0, 0, 1, 0},
                            {0, 0, 1, 0},
                            {0, 0, 1, 0},
                            {0, 0, 1, 0}
                    },
                    {
                            {0, 0, 0, 0},
                            {0, 0, 0, 0},
                            {1, 1, 1, 1},
                            {0, 0, 0, 0}
                    },
                    {
                            {0, 1, 0, 0},
                            {0, 1, 0, 0},
                            {0, 1, 0, 0},
                            {0, 1, 0, 0}
                    }
            },
            // J
            {
                    {
                            {1, 0, 0},
                            {1, 1, 1},
                            {0, 0, 0}
                    },
                    {
                            {0, 1, 1},
                            {0, 1, 0},
                            {0, 1, 0}
                    },
                    {
                            {0, 0, 0},
                            {1, 1, 1},
                            {0, 0, 1}
                    },
                    {
                            {0, 1, 0},
                            {0, 1, 0},
                            {1, 1, 0}
                    }
            },
            // L
            {
                    {
                            {0, 0, 1},
                            {1, 1, 1},
                            {0, 0, 0}
                    },
                    {
                            {0, 1, 0},
                            {0, 1, 0},
                            {0, 1, 1}
                    },
                    {
                            {0, 0, 0},
                            {1, 1, 1},
                            {1, 0, 0}
                    },
                    {
                            {1, 1, 0},
                            {0, 1, 0},
                            {0, 1, 0}
                    }
            },
            // O
            {
                    {
                            {0, 1, 1, 0},
                            {0, 1, 1, 0},
                            {0, 0, 0, 0}
                    }
            },
            // S
            {
                    {
                            {0, 1, 1},
                            {1, 1, 0},
                            {0, 0, 0}
                    },
                    {
                            {0, 1, 0},
                            {0, 1, 1},
                            {0, 0, 1}
                    },
                    {
                            {0, 0, 0},
                            {0, 1, 1},
                            {1, 1, 0}
                    },
                    {
                            {1, 0, 0},
                            {1, 1, 0},
                            {0, 1, 0}
                    }
            },
            // T
            {
                    {
                            {0, 1, 0},
                            {1, 1, 1},
                            {0, 0, 0}
                    },
                    {
                            {0, 1, 0},
                            {0, 1, 1},
                            {0, 1, 0}
                    },
                    {
                            {0, 0, 0},
                            {1, 1, 1},
                            {0, 1, 0}
                    },
                    {
                            {0, 1, 0},
                            {1, 1, 0},
                            {0, 1, 0}
                    }
            },
            // Z
            {
                    {
                            {1, 1, 0},
                            {0, 1, 1},
                            {0, 0, 0}
                    },
                    {
                            {0, 0, 1},
                            {0, 1, 1},
                            {0, 1, 0}
                    },
                    {
                            {0, 0, 0},
                            {1, 1, 0},
                            {0, 1, 1}
                    },
                    {
                            {0, 1, 0},
                            {1, 1, 0},
                            {1, 0, 0}
                    }
            }
    };

    // The number of different rotation states for each shape
    private final int[] NUMBER_OF_ROTATION_STATES = {4, 4, 4, 1, 4, 4, 4};

    /**
     * Gets the number of rotation states for the shape of this tetromino
     *
     * @return The number of rotation states
     */
    public int getNumberOfRotationStates() {
        return NUMBER_OF_ROTATION_STATES[this.shape.ordinal()];
    }

    public Shapes shape;
    public TetrominoSquare[][] squares;
    public TetrominoSquare.Colours colour;

    // 0 = 0 degrees, 1 = 90 degrees, 2 = 180 degrees, 3 = 270 degrees
    private int rotation = 0;

    /**
     * Gets the current rotation of this tetromino
     *
     * @return The rotation of this tetromino
     */
    public int getRotation() {
        return rotation;
    }

    /**
     * Rotates the tetromino by the specified amount
     *
     * @param rotation The amount to rotate the tetromino by (1-3)
     *                 1 = 90 degrees, 2 = 180 degrees, 3 = 270 degrees
     */
    public void setRotation(int rotation) {
        if (this.rotation == rotation % getNumberOfRotationStates()) {
            return;
        }
        this.rotation = rotation % getNumberOfRotationStates();

        // Update the squares
        for (int i = 0; i < TETROMINO_SHAPE_ARRAYS[shape.ordinal()][this.rotation].length; i++) {
            for (int j = 0; j < TETROMINO_SHAPE_ARRAYS[shape.ordinal()][this.rotation][i].length; j++) {
                if (TETROMINO_SHAPE_ARRAYS[shape.ordinal()][this.rotation][i][j] == 1) {
                    this.squares[i][j].state = TetrominoSquare.State.FALLING;
                    this.squares[i][j].colour = this.colour;
                } else {
                    this.squares[i][j] = new TetrominoSquare(TetrominoSquare.Colours.EMPTY,
                            TetrominoSquare.State.EMPTY);
                }
            }
        }
    }

    /**
     * Creates a new identical tetromino with the rotation and shape
     *
     * @return The new tetromino
     */
    public TetrominoShape clone() throws CloneNotSupportedException {
        TetrominoShape clone = (TetrominoShape) super.clone();
        clone.setRotation(this.rotation);
        return clone;
    }

    public TetrominoShape(Shapes shape) {
        this.shape = shape;
        this.colour = TetrominoSquare.Colours.values()[shape.ordinal()];

        // Initialise the array size to the size of the shape in tetrominoShapeArrays
        this.squares = new TetrominoSquare[TETROMINO_SHAPE_ARRAYS[shape.ordinal()][0].length]
                [TETROMINO_SHAPE_ARRAYS[shape.ordinal()][0][0].length];

        // Initialise the square array with the correct shape and data
        for (int i = 0; i < TETROMINO_SHAPE_ARRAYS[shape.ordinal()][0].length; i++) {
            for (int j = 0; j < TETROMINO_SHAPE_ARRAYS[shape.ordinal()][0][i].length; j++) {
                if (TETROMINO_SHAPE_ARRAYS[shape.ordinal()][0][i][j] == 1) {
                    this.squares[i][j] = new TetrominoSquare(TetrominoSquare.Colours.values()[shape.ordinal()],
                            TetrominoSquare.State.FALLING);
                } else {
                    this.squares[i][j] = new TetrominoSquare(TetrominoSquare.Colours.EMPTY,
                            TetrominoSquare.State.EMPTY);
                }
            }
        }
    }
}
