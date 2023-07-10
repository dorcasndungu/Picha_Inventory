package com.example.pichainventory;

import androidx.fragment.app.Fragment;

public interface SearchableFragment {
    void performSearch(String query);
    void resetData();
}


