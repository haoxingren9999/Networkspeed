package com.slt.networkspeed.mvp;



public interface  BasePresenter <V extends BaseView>{
    void attachView(V view);

    void detachView();
}
