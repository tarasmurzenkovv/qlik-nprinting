package com.nprinting.model.nprinting.response;

public class Response<T> {
    private Data<T> data;

    public Data<T> getData() {
        return data;
    }

    public void setData(final Data<T> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Response{" +
            "data=" + data +
            '}';
    }
}
