package dal;

import java.util.Objects;

public final class Member {
    private final int id;
    private final String name;

    private Member(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Member aMember(int id, String name) {
        return new Member(id, name);
    }

    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return id == member.id &&
                Objects.equals(name, member.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
