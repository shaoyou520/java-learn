package org.example.respond;


import lombok.Data;

@Data
public class Result<T> {
    private String code;
    private T t;
    private String message;

    public Result(String code, T t) {
        this.code = code;
        this.t = t;
    }

    public static Result success(Object t) {
        return new Result("200", t);
    }
}
