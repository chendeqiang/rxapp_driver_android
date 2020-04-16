package com.mxingo.driver.model;

public class StsEntity {

    /**
     * status : 200
     * AccessKeyId : STS.NU2sZi1n6cRNGUK9DaoEeDBbJ
     * AccessKeySecret : AdwUQWrBb39dKRSueui8VYXf79cRf4vZbuhVutsyq2Em
     * SecurityToken : CAISxgJ1q6Ft5B2yfSjIr5aHOODd3LEX1JClRXP63UQ0Y8pJq4fJqDz2IHxKf3NsAOEctPwynWlT7/0SlqB6T55OSAmcNZIoNy/cD/znMeT7oMWQweEubPTHcDHh5XeZsebWZ+LmNuu/Ht6md1HDkAJq3LL+bk/Mdle5MJqP+84FC9MMRVuAcCZhDtVbLRcAzcgBLin1NO2xChfwnnGyfE1zoVhVg2Rl9YGixtGd8hPEkGDizugcq/6yQP6eYtJrIY10XvqsweVybdCh6iNL7AVQ/6oE684h8Dzc7NaGGAsTsVfUccisq4Q3fVInPfViQfAe8KCjxMcV4LKDy97FrD9WJvxQXijlQ4St/dDJAuvBNKxiKeigYSiUg4rVbsSs6lh9OiIBRwpOess8LHhrEgArSTzcJaKh9UrDfgC5Ua+B3bGRQ0WOYZl8lBqAAbWenHSRj3IHA4vLGRjgriwooB0vXltf7Z2/8+iWCcZP6+e1O3GyN16adgKYBnanDUVHz22QZpy6iKLKxwKLDgsYbH2l4/QeC6jItAnk+FfX0j5HdeOT9U7rjP3N1WJ+PzGyrInvNSBLYWbvRjDgu2XArq+9TXCFEeOH1xO+yu1a
     * Expiration : 2020-04-13T09:22:30Z
     */

    public String status;
    public String AccessKeyId;
    public String AccessKeySecret;
    public String SecurityToken;
    public String Expiration;

    @Override
    public String toString() {
        return "{" +
                "status='" + status + '\'' +
                ", AccessKeyId='" + AccessKeyId + '\'' +
                ", AccessKeySecret='" + AccessKeySecret + '\'' +
                ", SecurityToken='" + SecurityToken + '\'' +
                ", Expiration='" + Expiration + '\'' +
                '}';
    }
}
