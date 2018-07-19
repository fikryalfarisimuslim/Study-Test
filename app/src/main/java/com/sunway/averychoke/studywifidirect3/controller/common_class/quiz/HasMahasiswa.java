package com.sunway.averychoke.studywifidirect3.controller.common_class.quiz;

import com.sunway.averychoke.studywifidirect3.model.Mahasiswa;

/**
 * Created by AveryChoke on 4/4/2017.
 */

// interface for view holder that contains a question
public interface HasMahasiswa {
    void setMahasiswa(Mahasiswa mahasiswa, int lastIndex);
}
