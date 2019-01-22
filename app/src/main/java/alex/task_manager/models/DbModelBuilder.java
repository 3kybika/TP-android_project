package alex.task_manager.models;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class DbModelBuilder<T> {

    protected abstract T mapper(Cursor cursor);
    protected boolean toBoolean(int value) {
        return value == 1;
    }

    public T buildOneInstance(Cursor cursor){
        T result;
        if(cursor.moveToFirst()) {
            try {
                result = mapper(cursor);
            } finally {
                cursor.close();
            }
        }
        else{
            return null;
        }
        return result;
    }

    public T buildOneInstance(Cursor cursor, int position){
        T result;
        if(cursor.move(position)) {
            try {
                result = mapper(cursor);
            } finally {
                cursor.close();
            }
        }
        else{
            return null;
        }
        return result;
    }

    public List<T> buildListInstance(Cursor cursor) {
        final List<T> resultList = new ArrayList<>();
        if(cursor.moveToFirst()) {
            try {
                do  {
                    resultList.add( mapper(cursor));
                }
                while(cursor.moveToNext());
            } finally {
                cursor.close();
            }
        }
        else{
            return Collections.emptyList();
        }
        return resultList;
    }

    public T buildCurrentInstance(Cursor cursor) {
        return mapper(cursor);
    }
}
