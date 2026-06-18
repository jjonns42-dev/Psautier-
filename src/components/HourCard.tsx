import React from 'react';
import { StyleSheet, Text, TouchableOpacity, View } from 'react-native';
import { Colors } from '../theme/colors';
import type { OfficeHour } from '../data/offices';

interface HourCardProps {
  office: OfficeHour;
  isCurrent?: boolean;
  onPress: () => void;
}

const HOUR_ICONS: Record<string, string> = {
  mesonikion: '🌑',
  orthros: '🌅',
  'premiere-heure': '☀️',
  'troisieme-heure': '🕘',
  'sixieme-heure': '🕛',
  'neuvieme-heure': '🕒',
  hesperinos: '🌆',
  apodipnon: '🌙',
};

function formatHour(h: number): string {
  return `${String(h).padStart(2, '0')}h00`;
}

export default function HourCard({ office, isCurrent = false, onPress }: HourCardProps) {
  return (
    <TouchableOpacity
      style={[styles.card, isCurrent && styles.cardCurrent]}
      onPress={onPress}
      activeOpacity={0.85}
    >
      <View style={styles.iconContainer}>
        <Text style={styles.icon}>{HOUR_ICONS[office.id] ?? '✝️'}</Text>
      </View>
      <View style={styles.content}>
        <Text style={[styles.name, isCurrent && styles.nameCurrent]}>{office.name}</Text>
        <Text style={[styles.nameGreek, isCurrent && styles.nameGreekCurrent]}>
          {office.nameGreek}
        </Text>
        <Text style={[styles.nameFr, isCurrent && styles.nameFrCurrent]}>{office.nameFr}</Text>
      </View>
      <View style={styles.right}>
        <Text style={[styles.hour, isCurrent && styles.hourCurrent]}>
          {formatHour(office.hour)}
        </Text>
        {isCurrent && (
          <View style={styles.badge}>
            <Text style={styles.badgeText}>En cours</Text>
          </View>
        )}
      </View>
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  card: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: Colors.white,
    borderRadius: 12,
    marginHorizontal: 16,
    marginVertical: 6,
    padding: 14,
    borderLeftWidth: 4,
    borderLeftColor: Colors.border,
    shadowColor: Colors.shadow,
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 1,
    shadowRadius: 4,
    elevation: 3,
  },
  cardCurrent: {
    borderLeftColor: Colors.gold,
    backgroundColor: '#FFFDF5',
  },
  iconContainer: {
    width: 44,
    height: 44,
    borderRadius: 22,
    backgroundColor: Colors.creamDark,
    alignItems: 'center',
    justifyContent: 'center',
    marginRight: 12,
  },
  icon: {
    fontSize: 22,
  },
  content: {
    flex: 1,
  },
  name: {
    fontSize: 16,
    fontWeight: '700',
    color: Colors.textPrimary,
    letterSpacing: 0.3,
  },
  nameCurrent: {
    color: Colors.burgundy,
  },
  nameGreek: {
    fontSize: 12,
    color: Colors.textSecondary,
    fontStyle: 'italic',
    marginTop: 1,
  },
  nameGreekCurrent: {
    color: Colors.goldDark,
  },
  nameFr: {
    fontSize: 12,
    color: Colors.textSecondary,
    marginTop: 2,
  },
  nameFrCurrent: {
    color: Colors.textPrimary,
  },
  right: {
    alignItems: 'flex-end',
    gap: 4,
  },
  hour: {
    fontSize: 13,
    fontWeight: '600',
    color: Colors.textSecondary,
  },
  hourCurrent: {
    color: Colors.gold,
    fontWeight: '700',
  },
  badge: {
    backgroundColor: Colors.gold,
    borderRadius: 10,
    paddingHorizontal: 8,
    paddingVertical: 2,
  },
  badgeText: {
    color: Colors.white,
    fontSize: 10,
    fontWeight: '700',
  },
});
