public class Utils {
    static class Pair<T1, T2> {
        public T1 first;
        public T2 second;

        public Pair(T1 first, T2 second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public String toString() {
            return "Utils.Pair(" + first.toString() + ", " + second.toString() + ")";
        }

        @Override
        public int hashCode() {
            return first.hashCode() ^ second.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Pair<?, ?> pair)) {
                return false;
            }
            return this.first.equals(pair.first) && this.second.equals(pair.second);
        }
    }
}
