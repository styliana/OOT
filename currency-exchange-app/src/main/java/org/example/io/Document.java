package org.example.io;

import org.example.model.ExchangeTable;

public interface Document {
    ExchangeTable getTable(String content) throws Exception;
}
