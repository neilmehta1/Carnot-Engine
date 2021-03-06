package com.github.neilmehta1.carnotengine.boardstate;

import com.github.neilmehta1.carnotengine.utils.MoveTuple;

import java.util.LinkedList;
import java.util.List;

import static com.github.neilmehta1.carnotengine.boardstate.Position.ChessPieces.*;


public class Position implements Comparable<Position> {


    public enum ChessPieces {
        brook,
        bknight,
        bbishop,
        bqueen,
        bking,
        bpawn,
        wrook,
        wknight,
        wbishop,
        wqueen,
        wking,
        wpawn,
        empty
    }

    private static int[] bpawnPST = new int[64];
    private static int[] bpawnEndPST = new int[64];
    private static int[] brookPST = new int[64];
    private static int[] brookEndPST = new int[64];
    private static int[] bknightPST = new int[64];
    private static int[] bknightEndPST = new int[64];
    private static int[] bbishopPST = new int[64];
    private static int[] bbishopEndPST = new int[64];
    private static int[] bqueenPST = new int[64];
    private static int[] bqueenEndPST = new int[64];
    private static int[] bkingMidPST = new int[64];
    private static int[] bkingEndPST = new int[64];
    private static int[] wpawnPST = new int[64];
    private static int[] wpawnEndPST = new int[64];
    private static int[] wrookPST = new int[64];
    private static int[] wrookEndPST = new int[64];
    private static int[] wknightPST = new int[64];
    private static int[] wknightEndPST = new int[64];
    private static int[] wbishopPST = new int[64];
    private static int[] wbishopEndPST = new int[64];
    private static int[] wqueenPST = new int[64];
    private static int[] wqueenEndPST = new int[64];
    private static int[] wkingMidPST = new int[64];
    private static int[] wkingEndPST = new int[64];


    private ChessPieces[] allPieces = new ChessPieces[64];
    private ChessPieces[] whitePieces = new ChessPieces[64];
    private ChessPieces[] blackPieces = new ChessPieces[64];
    private boolean whiteCanCastle = true;
    private boolean blackCanCastle = true;
    private boolean whitesMove = true;
    private int blackKingPos = 60;
    private int whiteKingPos = 4;
    private MoveTuple lastMove;
    private int score = 0;
    private int halfMoveNumber = 0;
    private List<MoveTuple> moveList = new LinkedList<MoveTuple>(); // last in list is most recent move.
    private int enPassant;


    private boolean A1RookMoved = false;
    private boolean H1RookMoved = false;
    private boolean A8RookMoved = false;
    private boolean H8RookMoved = false;

    private boolean wqueenAlive = true;
    private boolean bqueenAlive = true;

    public List<MoveTuple> recentMoves = new LinkedList<MoveTuple>();


    public Position(Position position) {
        allPieces = copyBoard(position.allPieces);
        whitePieces = copyBoard(position.whitePieces);
        blackPieces = copyBoard(position.blackPieces);
        whiteCanCastle = position.whiteCanCastle;
        blackCanCastle = position.blackCanCastle;
        enPassant = position.enPassant;
        whitesMove = position.whitesMove;
        blackKingPos = position.blackKingPos;
        whiteKingPos = position.whiteKingPos;
        if (position.lastMove != null) {
            lastMove = position.lastMove;
        }
        score = position.score;
        halfMoveNumber = position.halfMoveNumber;
        moveList = copyList(position.moveList);
        recentMoves = copyList(position.recentMoves);
        A1RookMoved = position.A1RookMoved;
        H1RookMoved = position.H1RookMoved;
        A8RookMoved = position.A8RookMoved;
        H8RookMoved = position.H8RookMoved;
        wqueenAlive = position.wqueenAlive;
        bqueenAlive = position.bqueenAlive;
    }

    public Position(Position position, boolean setWhitesMoveTo) {
        this(position);
        this.whitesMove = setWhitesMoveTo;
    }

    public Position() {
        initAll(this.allPieces);
        initWhite(this.whitePieces);
        initBlack(this.blackPieces);
        initPST2();
    }

    @Override
    public int compareTo(Position o) {
        return ((Integer) score).compareTo(o.score);
    }

    public Position movePiece(MoveTuple move) {
        Position position = new Position(this);
        int from = move.from;
        int to = move.to;
        ChessPieces piece;
        int[] scoreTable;
        position.lastMove = move;
        if (position.whitesMove) {
            piece = position.whitePieces[from];
            scoreTable = getTable(piece);
            position.score -= scoreTable[to] - scoreTable[from];

            if (piece == wpawn && (from + 7 == to || from + 9 == to) && position.enPassant != -2 && position.blackPieces[to] == empty) {
                position.score -= getTable(position.blackPieces[to - 8])[to - 8];
                position.blackPieces[to - 8] = empty;
                position.allPieces[to - 8] = empty;
            }

            if (piece == wpawn && from + 16 == to) {
                position.enPassant = to;
            } else {
                position.enPassant = -2;
            }

            if (position.whitePieces[from] == wking) {
                position.whiteKingPos = to;

                if (from / 8 == 0 && (to % 8 == 2 || to % 8 == 6) && position.whiteCanCastle) {
                    if (to % 8 == 2) {
                        position.allPieces[0] = empty;
                        position.whitePieces[0] = empty;
                        position.allPieces[3] = wrook;
                        position.whitePieces[3] = wrook;
                        position.score -= wrookPST[3] - wrookPST[0];
                    } else {
                        position.allPieces[7] = empty;
                        position.whitePieces[7] = empty;
                        position.allPieces[5] = wrook;
                        position.whitePieces[5] = wrook;
                        position.score -= wrookPST[5] - wrookPST[7];
                    }
                }
                position.whiteCanCastle = false;


            }
            if (position.blackPieces[to] != empty) {
                position.score -= getTable(position.blackPieces[to])[to];
                if (position.blackPieces[to]==bqueen){
                    position.bqueenAlive = false;
                }
                position.blackPieces[to] = empty;
            }


            if (to / 8 == 7 && position.whitePieces[from] == wpawn) {
                position.score -= wqueenPST[to] - wpawnPST[from];
                position.whitePieces[to] = wqueen;
                position.allPieces[to] = wqueen;
            } else {
                position.whitePieces[to] = position.whitePieces[from];
                position.allPieces[to] = position.whitePieces[to];
            }
            position.whitePieces[from] = empty;
            position.allPieces[from] = empty;


        } else {
            piece = position.blackPieces[from];
            scoreTable = getTable(piece);
            position.score += scoreTable[to] - scoreTable[from];

            if (piece == bpawn && (from - 7 == to || from - 9 == to) && position.enPassant != -2 && position.whitePieces[to] == empty) {
                position.score += getTable(position.whitePieces[to + 8])[to + 8];
                position.whitePieces[to + 8] = empty;
                position.allPieces[to + 8] = empty;
            }

            if (piece == bpawn && from - 16 == to) {
                position.enPassant = to;
            } else {
                position.enPassant = -2;
            }

            if (position.blackPieces[from] == bking) {
                position.blackKingPos = to;
                if (from / 8 == 7 && (to % 8 == 2 || to % 8 == 6) && position.blackCanCastle) {
                    if (to % 8 == 2) {
                        position.allPieces[56] = empty;
                        position.blackPieces[56] = empty;
                        position.allPieces[59] = brook;
                        position.blackPieces[59] = brook;
                        position.score += brookPST[59] - brookPST[56];
                    } else {
                        position.allPieces[63] = empty;
                        position.blackPieces[63] = empty;
                        position.allPieces[61] = brook;
                        position.blackPieces[61] = brook;
                        position.score += wrookPST[61] - wrookPST[63];
                    }
                }
                position.blackCanCastle = false;


            }
            if (position.whitePieces[to] != empty) {
                position.score += getTable(position.whitePieces[to])[to];
                if (position.whitePieces[to]==wqueen){
                    position.wqueenAlive = false;
                }
                position.whitePieces[to] = empty;
            }


            if (to / 8 == 0 && position.blackPieces[from] == bpawn) {
                position.score += bqueenPST[to];
                position.blackPieces[to] = bqueen;
                position.allPieces[to] = bqueen;
            } else {
                position.blackPieces[to] = position.blackPieces[from];
                position.allPieces[to] = position.blackPieces[to];
            }
            position.blackPieces[from] = empty;
            position.allPieces[from] = empty;
        }

        if (position.whitePieces[0] != wrook) {
            position.A1RookMoved = true;
        }
        if (position.whitePieces[7] != wrook) {
            position.H1RookMoved = true;
        }
        if (position.blackPieces[56] != brook) {
            position.A8RookMoved = true;
        }
        if (position.blackPieces[63] != brook) {
            position.H8RookMoved = true;
        }

        position.whitesMove = !position.whitesMove;
        position.halfMoveNumber++;
        position.moveList.add(move);
        position.recentMoves.add(move);
        position.lastMove = new MoveTuple(move);
        /*
        if (!position.wqueenAlive&&!position.bqueenAlive){
            wpawnPST = wpawnEndPST;
            wrookPST = wrookEndPST;
            wknightPST = wknightEndPST;
            wbishopPST = wbishopEndPST;
            wkingMidPST = wkingEndPST;
            wqueenPST = wqueenEndPST;

            bpawnPST = bpawnEndPST;
            brookPST = brookEndPST;
            bknightPST = bknightEndPST;
            bbishopPST = bbishopEndPST;
            bkingMidPST = bkingEndPST;
            bqueenPST = bqueenEndPST;
        }
        */
        return position;
    }

    private static ChessPieces[] copyBoard(ChessPieces[] toCopy) {
        ChessPieces[] output = new ChessPieces[64];
        for (int i = 0; i <= 63; i++) {
            output[i] = toCopy[i];
        }
        return output;
    }

    public static <T> List<T> copyList(List<T> obList) {
        LinkedList<T> toReturn = new LinkedList<T>();
        for (T obj : obList) {
            toReturn.add(obj);
        }
        return toReturn;
    }

    private static boolean pieceIsWhite(ChessPieces piece) {
        return piece == wpawn || piece == wrook || piece == wknight || piece == wbishop || piece == wqueen || piece == wking;
    }

    public static boolean pieceIsBlack(ChessPieces piece) {
        return piece != empty && !Position.pieceIsWhite(piece);
    }

    private static int[] getTable(Position.ChessPieces piece) {
        if (piece == wpawn || piece == bpawn) {
            if (piece == bpawn) {
                return bpawnPST;
            } else {
                return wpawnPST;
            }
        } else if (piece == wrook || piece == brook) {
            if (piece == brook) {
                return brookPST;
            } else {
                return wrookPST;
            }
        } else if (piece == wknight || piece == bknight) {
            if (piece == bknight) {
                return bknightPST;
            } else {
                return wknightPST;
            }
        } else if (piece == wbishop || piece == bbishop) {
            if (piece == bbishop) {
                return bbishopPST;
            } else {
                return wbishopPST;
            }
        } else if (piece == wqueen || piece == bqueen) {
            if (piece == bqueen) {
                return bqueenPST;
            } else {
                return wqueenPST;
            }
        } else if (piece == wking || piece == bking) {
            if (piece == bking) {
                return bkingMidPST;
            } else {
                return wkingMidPST;
            }
        } else {
            throw new RuntimeException("empty piece returned");
        }
    }


    private static void initAll(ChessPieces[] initChessPieces) {

        initEmptyBoard(initChessPieces);

        initChessPieces[0] = wrook;
        initChessPieces[1] = wknight;
        initChessPieces[2] = wbishop;
        initChessPieces[3] = wqueen;
        initChessPieces[4] = wking;
        initChessPieces[5] = wbishop;
        initChessPieces[6] = wknight;
        initChessPieces[7] = wrook;

        for (int i = 8; i <= 15; i++) {
            initChessPieces[i] = wpawn;
            initChessPieces[63 - i] = bpawn;
        }

        initChessPieces[56] = brook;
        initChessPieces[56 + 1] = bknight;
        initChessPieces[56 + 2] = bbishop;
        initChessPieces[56 + 3] = bqueen;
        initChessPieces[56 + 4] = bking;
        initChessPieces[56 + 5] = bbishop;
        initChessPieces[56 + 6] = bknight;
        initChessPieces[56 + 7] = brook;


    }

    private static void initWhite(ChessPieces[] initChessPieces) {

        initEmptyBoard(initChessPieces);

        initChessPieces[0] = wrook;
        initChessPieces[1] = wknight;
        initChessPieces[2] = wbishop;
        initChessPieces[3] = wqueen;
        initChessPieces[4] = wking;
        initChessPieces[5] = wbishop;
        initChessPieces[6] = wknight;
        initChessPieces[7] = wrook;

        for (int i = 8; i <= 15; i++) {
            initChessPieces[i] = wpawn;
        }

    }

    private static void initBlack(ChessPieces[] initChessPieces) {

        initEmptyBoard(initChessPieces);

        for (int i = 8; i <= 15; i++) {
            initChessPieces[63 - i] = bpawn;
        }

        initChessPieces[56] = brook;
        initChessPieces[56 + 1] = bknight;
        initChessPieces[56 + 2] = bbishop;
        initChessPieces[56 + 3] = bqueen;
        initChessPieces[56 + 4] = bking;
        initChessPieces[56 + 5] = bbishop;
        initChessPieces[56 + 6] = bknight;
        initChessPieces[56 + 7] = brook;
    }

    private static void initEmptyBoard(ChessPieces[] board) {
        for (int i = 0; i <= 63; i++) {
            board[i] = empty;
        }
    }


    private static void initPST2() {


        wpawnPST = new int[]{
                0, 0, 0, 0, 0, 0, 0, 0,
                -21, -16, -6, -1, -1, -6, -16, -21,
                -21, -16, -6, 4, 4, -6, -16, -21,
                -21, -16, -1, 9, 9, -1, -16, -21,
                -14, -8, 6, 17, 17, 6, -8, -14,
                -5, 1, 14, 29, 29, 14, 1, -5,
                7, 11, 23, 39, 39, 23, 11, 7,
                0, 0, 0, 0, 0, 0, 0, 0

        };

        wpawnEndPST = new int[]{
                0, 0, 0, 0, 0, 0, 0, 0,
                5, -10, -20, -25, -25, -20, -10, 5,
                5, -10, -20, -25, -25, -20, -10, 5,
                10, -5, -15, -20, -20, -15, -5, 10,
                18, 2, -8, -15, -15, -8, 2, 18,
                30, 14, 1, -10, -10, 1, 14, 30,
                45, 30, 16, 5, 5, 16, 30, 45,
                0, 0, 0, 0, 0, 0, 0, 0
        };


        wknightPST = new int[]{
                -69, -19, -24, -14, -14, -24, -19, -69,
                -54, -39, -9, 11, 11, -9, -39, -54,
                -39, 1, 31, 21, 21, 31, 1, -39,
                -39, 11, 41, 36, 36, 41, 11, -39,
                -39, 41, 51, 51, 51, 51, 41, -39,
                -39, 46, 61, 71, 71, 61, 46, -39,
                -39, 21, 41, 41, 41, 41, 21, -39,
                -59, -39, -29, -29, -29, -29, -39, -59

        };

        wknightEndPST = new int[]{
                -63, -53, -43, -43, -43, -43, -53, -63,
                -53, -43, 18, 28, 28, 18, -43, -53,
                -43, 18, 48, 38, 38, 48, 18, -43,
                -43, 38, 58, 68, 68, 58, 38, -43,
                -43, 38, 73, 78, 78, 73, 38, -43,
                -43, 28, 78, 73, 73, 78, 28, -43,
                -53, -43, 38, 48, 48, 38, -43, -53,
                -63, -53, -43, -43, -43, -43, -53, -63
        };


        wbishopPST = new int[]{
                -30, -25, -20, -20, -20, -20, -25, -30,
                -28, 11, 6, 1, 1, 6, 11, -28,
                -25, 6, 16, 11, 11, 16, 6, -25,
                1, 1, 16, 21, 21, 16, 1, 1,
                1, 21, 21, 26, 26, 21, 21, 1,
                1, 11, 21, 26, 26, 21, 11, 1,
                -10, 11, 1, 1, 1, 1, 11, -10,
                -20, -18, -16, -14, -14, -16, -18, -20

        };

        wbishopEndPST = new int[]{
                -38, -18, -8, 2, 2, -8, -18, -38,
                -18, -8, 2, 7, 7, 2, -8, -18,
                -8, 2, 10, 12, 12, 10, 2, -8,
                2, 12, 16, 20, 20, 16, 12, 2,
                2, 12, 17, 22, 22, 17, 12, 2,
                -8, 2, 20, 22, 22, 20, 2, -8,
                -18, -8, 0, 12, 12, 0, -8, -18,
                -38, -18, -8, 2, 2, -8, -18, -38
        };


        wrookPST = new int[]{
                -8, -6, 2, 7, 7, 2, -6, -8,
                -8, -6, 2, 7, 7, 2, -6, -8,
                -8, -6, 6, 7, 7, 6, -6, -8,
                -8, -6, 6, 7, 7, 6, -6, -8,
                -8, -6, 6, 8, 8, 6, -6, -8,
                -8, -6, 6, 10, 10, 6, -6, -8,
                2, 2, 7, 12, 12, 7, 2, 2,
                -8, -6, 2, 7, 7, 2, -6, -8

        };

        wrookEndPST = new int[]{
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0
        };


        wqueenPST = new int[]{
                -26, -16, -6, 4, 4, -6, -16, -26,
                -16, -11, -1, 4, 4, -1, -11, -16,
                -6, -6, -1, 4, 4, -1, -6, -6,
                4, 4, 4, 4, 4, 4, 4, 4,
                4, 4, 4, 4, 4, 4, 4, 4,
                4, 4, 4, 4, 4, 4, 4, 4,
                4, 4, 4, 4, 4, 4, 4, 4,
                4, 4, 4, 4, 4, 4, 4, 4

        };

        wqueenEndPST = new int[]{
                -46, -41, -31, -26, -26, -31, -41, -46,
                -31, -26, -16, -6, -6, -16, -26, -31,
                -16, -1, 14, 24, 24, 14, -1, -16,
                -6, 9, 24, 34, 34, 24, 9, -6,
                -6, 9, 24, 34, 34, 24, 9, -6,
                -6, 9, 24, 34, 34, 24, 9, -6,
                -16, 4, 19, 29, 29, 19, 4, -16,
                -26, -6, -1, 4, 4, -1, -6, -26
        };


        wkingMidPST = new int[]{
                -20, 0, 0, -10, -10, 0, 0, -20,
                -30, -30, -30, -35, -35, -30, -30, -30,
                -40, -40, -45, -50, -50, -45, -40, -40,
                -50, -50, -55, -60, -60, -55, -50, -50,
                -55, -55, -60, -70, -70, -60, -55, -55,
                -55, -55, -60, -70, -70, -60, -55, -55,
                -55, -55, -60, -70, -70, -60, -55, -55,
                -55, -55, -60, -70, -70, -60, -55, -55

        };

        wkingEndPST = new int[]{
                -30, -25, -15, -10, -10, -15, -25, -30,
                -15, -10, 0, 10, 10, 0, -10, -15,
                0, 15, 30, 40, 40, 30, 15, 0,
                10, 25, 40, 50, 50, 40, 25, 10,
                10, 25, 40, 50, 50, 40, 25, 10,
                10, 25, 40, 50, 50, 40, 25, 10,
                0, 20, 35, 45, 45, 35, 20, 0,
                -10, 10, 15, 20, 20, 15, 10, -10

        };

        for (int i = 0; i <= 63; i++) {
            wpawnPST[i] = wpawnPST[i] + 100;
            wrookPST[i] = wrookPST[i] + 500;
            wknightPST[i] = wknightPST[i] + 320;
            wbishopPST[i] = wbishopPST[i] + 330;
            wqueenPST[i] = wqueenPST[i] + 900;
            wkingMidPST[i] = wkingMidPST[i] + 20000;
            wkingEndPST[i] = wkingEndPST[i] + 20000;
            wpawnEndPST[i] = wpawnEndPST[i] + 100;
            wrookEndPST[i] = wrookEndPST[i] + 500;
            wknightEndPST[i] = wknightEndPST[i] + 320;
            wbishopEndPST[i] = wbishopEndPST[i] + 330;
            wqueenEndPST[i] = wqueenEndPST[i] + 900;
            wkingMidPST[i] = wkingMidPST[i] + 20000;
            wkingEndPST[i] = wkingEndPST[i] + 20000;
        }

        bpawnPST = reverseTable(wpawnPST);
        bpawnEndPST = reverseTable(wpawnEndPST);
        brookPST = reverseTable(wrookPST);
        brookEndPST = reverseTable(wrookEndPST);
        bknightPST = reverseTable(wknightPST);
        bknightEndPST = reverseTable(wknightEndPST);
        bbishopPST = reverseTable(wbishopPST);
        bbishopEndPST = reverseTable(wbishopEndPST);
        bqueenPST = reverseTable(wqueenPST);
        bqueenEndPST = reverseTable(wqueenEndPST);
        bkingMidPST = reverseTable(wkingMidPST);
        bkingEndPST = reverseTable(wkingEndPST);
    }

    private static int[] reverseTable(int[] whiteTable) {
        int[] toReturn = new int[64];
        for (int i = 0; i <= 63; i++) {
            toReturn[i] = whiteTable[63 - i];
        }
        return toReturn;
    }


    public int getWhiteKingPos() {
        return whiteKingPos;
    }

    public int getBlackKingPos() {
        return blackKingPos;
    }

    public boolean isWhitesMove() {
        return whitesMove;
    }

    public boolean isBlackCanCastle() {
        return blackCanCastle;
    }

    public boolean isWhiteCanCastle() {
        return whiteCanCastle;
    }

    public ChessPieces[] getWhitePieces() {
        return whitePieces;
    }

    public ChessPieces[] getBlackPieces() {
        return blackPieces;
    }

    public ChessPieces[] getAllPieces() {
        return allPieces;
    }

    public MoveTuple getLastMove() {
        return lastMove;
    }

    public int getScore() {
        return score;
    }

    public int getHalfMoveNumber() {
        return halfMoveNumber;
    }

    public List<MoveTuple> getMoveList() {
        return moveList;
    }

    public int getEnPassant() {
        return enPassant;
    }

    public boolean isA1RookMoved() {
        return A1RookMoved;
    }

    public boolean isH1RookMoved() {
        return H1RookMoved;
    }

    public boolean isA8RookMoved() {
        return A8RookMoved;
    }

    public boolean isH8RookMoved() {
        return H8RookMoved;
    }

}
