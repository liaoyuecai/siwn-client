package com.swin.client;

class MessageIdentify {
    static final int REGISTER_MAP = 0x0000;
    static final int REGISTER_QUEUE = 0x0001;
    static final int SUBSCRIBE_INIT = 0x0100;
    static final int SUBSCRIBE_APPOINT = 0x0101;
    static final int GET_TREE_MAP_DATA = 0x0201;
    static final int GET_TREE_MAP_DATA_OK = 0x0211;
    static final int GET_TREE_MAP_DATA_FAILED = 0x0221;
    static final int PUT_TREE_MAP_DATA = 0x0202;
    static final int PUT_TREE_MAP_DATA_OK = 0x0212;
    static final int PUT_TREE_MAP_DATA_FAILED = 0x0222;
    static final int CONNECT_OK = 0x0301;
    static final int CONNECT_EXCEPTION = 0x03FF;
}
